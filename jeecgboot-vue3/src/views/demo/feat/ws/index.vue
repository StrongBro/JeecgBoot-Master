<template>
  <PageWrapper title="串口连接测试">
    <div class="flex">
      <div class="w-1/3 bg-white p-4">
        <div class="flex items-center">
          <span class="text-lg font-medium mr-4"> 连接状态: </span>
          <Tag :color="getTagColor">{{ status }}</Tag>
        </div>
        <hr class="my-4" />
        <div class="flex items-center mt-4 space-x-4">
          <span class="w-24 text-lg font-medium mr-4">可用串口:</span>
          <a-select v-model:value="portType" class="flex-1" :loading="loading">
            <a-select-option v-for="port in portList" :key="port.id" :value="port.name">
              {{ port.name }}
            </a-select-option>
          </a-select>
          <span class="w-24 text-lg font-medium mr-4">波特率:</span>
          <a-select v-model:value="baudRate" class="flex-1" :loading="loading">
            <a-select-option v-for="baud in baudList" :key="baud.id" :value="baud.value">
              {{ baud.value }}
            </a-select-option>
          </a-select>
        </div>
        <div class="flex mt-4">
          <a-input v-model:value="server" disabled>
            <template #addonBefore> 服务地址 </template>
          </a-input>
          <a-button :type="getIsOpen ? 'danger' : 'primary'" @click="toggle">
            {{ getIsOpen ? '关闭连接' : '开启连接' }}
          </a-button>
        </div>
        <p class="text-lg font-medium mt-4">指令发送</p>
        <hr class="my-4" />

        <InputTextArea placeholder="输入指令" :disabled="!getIsOpen" v-model:value="sendValue" allowClear />

        <a-button type="primary" block class="mt-4" :disabled="!getIsOpen" @click="handlerSend"> 发送 </a-button>
      </div>

      <div class="w-2/3 bg-white ml-4 p-4">
        <span class="text-lg font-medium mr-4"> 消息记录: </span>
        <hr class="my-4" />

        <div class="max-h-80 overflow-auto">
          <ul>
            <li v-for="item in getList" class="mt-2" :key="item.time">
              <div class="flex items-center">
                <span class="mr-2 text-primary font-medium">收到消息:</span>
                <span>{{ formatToDateTime(item.time) }}</span>
              </div>
              <div>
                {{ item.res }}
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="mt-4 bg-white p-4">
      <BasicForm @register="registerForm" />
      <a-modal v-model:visible="visible" title="弹窗示例" @ok="handleOk">
        <p>这是一个基础弹窗内容</p>
      </a-modal>
    </div>
  </PageWrapper>
</template>
<script lang="ts">
  import { defineComponent, reactive, watchEffect, watch, computed, toRefs, ref } from 'vue';
  import { Tag, Input, Modal } from 'ant-design-vue';
  import { PageWrapper } from '/@/components/Page';
  import { useWebSocket } from '@vueuse/core';
  import { formatToDateTime } from '/@/utils/dateUtil';
  import { getToken } from '/@/utils/auth';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '@/hooks/web/useMessage';
  import { getAvailablePorts } from './index.api';
  import { defHttp } from '@/utils/http/axios';

  export default defineComponent({
    components: {
      PageWrapper,
      [Input.name]: Input,
      InputTextArea: Input.TextArea,
      Tag,
      BasicForm,
      [Modal.name]: Modal,
    },
    setup() {
      const { createMessage, notification } = useMessage();
      const state = reactive({
        server: 'ws://localhost:8080/jeecg-boot/SerialWebSocket/com1/9600',
        basePrefix: 'ws://localhost:8080/jeecg-boot/SerialWebSocket',
        sendValue: '',
        recordList: [] as { id: number; time: number; res: string }[],
        portType: '',
        portList: [] as { id: number; name: string }[],
        baudRate: '',
        baudList: [
          { id: 1, value: '9600' },
          { id: 2, value: '115200' },
          { id: 3, value: '230400' },
        ],
        loading: false,
      });
      const { status, data, send, close, open } = useWebSocket(() => state.server, {
        autoReconnect: false,
        heartbeat: false,
        immediate: false,
        protocols: [getToken()],
      });
      watchEffect(() => {
        if (data.value) {
          try {
            const { portName, msg } = JSON.parse(data.value);
            state.recordList.push({
              res: msg,
              id: Date.now(),
              time: new Date().getTime(),
            });
            notification.success({
              message: '串口【' + portName + '】消息:',
              description: msg,
            });
          } catch (error) {
            state.recordList.push({
              res: data.value,
              id: Date.now(),
              time: new Date().getTime(),
            });
          }
        }
      });
      const getIsOpen = computed(() => status.value === 'OPEN');
      const getTagColor = computed(() => (getIsOpen.value ? 'success' : 'red'));

      const getList = computed(() => {
        return [...state.recordList].reverse();
      });

      function handlerSend() {
        send(state.sendValue);
        state.sendValue = '';
      }

      function toggle() {
        console.log('ws url====' + state.server);
        // console.log(state.portType);
        // console.log(state.baudRate);
        if (getIsOpen.value) {
          close();
        } else {
          state.server = state.basePrefix + '/' + state.portType + '/' + state.baudRate;
          open();
        }
      }
      const loadPorts = async () => {
        try {
          state.loading = true;
          const ports = await defHttp.get({ url: '/serial/getAvailablePorts' });
          state.portList = ports.map((name: string, index: number) => ({ id: index + 1, name }));
        } catch (error) {
          createMessage.error('加载串口列表失败');
          // notification.success({
          //   message: 'Tip',
          //   description: 'content message...',
          // });
        } finally {
          state.loading = false;
        }
      };
      loadPorts();
      const visible = ref(false);
      const [registerForm] = useForm({
        schemas: [
          {
            field: 'select',
            label: '下拉列表',
            component: 'Select',
            componentProps: {
              options: [
                { label: '选项1', value: '1' },
                { label: '选项2', value: '2' },
                { label: '选项3', value: '3' },
              ],
            },
          },
          {
            field: 'popup',
            label: '弹窗按钮',
            component: 'Input',
            slot: 'popup',
            // render: () => {
            //   return h(Button, {
            //     onClick: () => (visible.value = true),
            //     type: 'primary',
            //   }, () => '打开弹窗');
            // },
          },
        ],
      });

      function handleOk() {
        visible.value = false;
      }

      return {
        status,
        formatToDateTime,
        ...toRefs(state),
        handlerSend,
        getList,
        toggle,
        getIsOpen,
        getTagColor,
        visible,
        registerForm,
        handleOk,
      };
    },
  });
</script>
