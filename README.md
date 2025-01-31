# AsyncHTTPClient

This class mediator can be used to invoke a backend service inside the class mediator using java.net.http package (introduced in Java 11). This uses sendAsync(HttpRequest, BodyHandler) method which sends the request and receives the response asynchronously. To engage the class mediator you need  to use a custom mediation policy as below and then use the respond mediator. Make sure to change the backend URL as you need.

<ul>
<li>connectionTimeout: the time to establish the connection with the remote host</li>
<li>socketTimeout: the time waiting for data – after establishing the connection; maximum time of inactivity between two data packets
</li>
<li>backEndUrl: backend url</li>
</ul>

```
<sequence xmlns="http://ws.apache.org/ns/synapse" name="custom_policy">
        <class name="org.custom.CustomHttpClient">
           <property name="initialUrl" value="https://run.mocky.io/v3/7fdb6b65-6e71-4c44-bc37-06b0f844e201"/>
        </class>
   <respond/>
</sequence>
