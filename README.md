测试步骤:
1.IDEA 里面直接启动ShopApplication
2.调用com.salesmanager.rest.service.ActuatorRestServiceTest#testPostActuatorShutdown方法,没有异常抛出,证明单元测试通过

具体POST 请求实现,参见org.geektimes.rest.client.HttpPostInvocation
