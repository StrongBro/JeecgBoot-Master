package org.jeecg.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

// 定制的负载均衡策略
public class MyCustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(MyCustomLoadBalancer.class);

    private final String serviceId;


    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    /**
     * @param serviceInstanceListSupplierProvider a provider of
     * {@link ServiceInstanceListSupplier} that will be used to get available instances
     * @param serviceId id of the service for which to choose an instance
     */
    public MyCustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }
        // 自定义负载均衡策略

        //获取 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = request.getRemoteAddr();
        log.info("用户 IP：" + ipAddress);
        int hash = ipAddress.hashCode();
        // IP 哈希负载均衡算法
        int index = hash % instances.size();
        // 得到服务实例的方法
        ServiceInstance instance = instances.get(index);

        return new DefaultResponse(instance);
    }

}
