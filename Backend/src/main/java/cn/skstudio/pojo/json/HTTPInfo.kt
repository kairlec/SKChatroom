package cn.skstudio.pojo.json

import cn.skstudio.utils.Network
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HTTPInfo(request: HttpServletRequest, response: HttpServletResponse) {
    @JSONField(name = "Scheme", ordinal = 7)
    val Scheme: String?
    @JSONField(name = "Proto", ordinal = 8)
    val Proto: String?
    @JSONField(name = "ResponseStatus", ordinal = 12)
    val ResponseStatus: Int?
    @JSONField(name = "URL", ordinal = 0)
    val URL: String?
    @JSONField(name = "URI", ordinal = 1)
    val URI: String?
    @JSONField(name = "QueryString", ordinal = 2)
    val QueryString: String?
    @JSONField(name = "RemoteIP", ordinal = 3)
    val RemoteIP: String?
    @JSONField(name = "RemoteUser", ordinal = 4)
    val RemoteUser: String?
    @JSONField(name = "Method", ordinal = 5)
    val Method: String?
    @JSONField(name = "WebName", ordinal = 6)
    val WebName: String?
    @JSONField(name = "Headers", ordinal = 9)
    val Headers: MutableMap<String, String?>?
    @JSONField(name = "Parameters", ordinal = 11)
    val Parameters: MutableMap<String, String?>?
    @JSONField(name = "Cookies", ordinal = 10)
    val Cookies: Array<Cookie>?

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }

    init {
        URL = URLDecoder.decode(request.requestURL.toString(), StandardCharsets.UTF_8)
        URI = URLDecoder.decode(request.requestURI, StandardCharsets.UTF_8)
        QueryString = request.queryString
        RemoteIP = Network.getIpAddress(request)
        Method = request.method
        WebName = request.contextPath
        RemoteUser = request.remoteUser
        Headers = HashMap()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val name = headerNames.nextElement()
            val value = request.getHeader(name)
            Headers[name] = value
        }
        Parameters = HashMap()
        val parameterNames = request.parameterNames
        while (parameterNames.hasMoreElements()) {
            val name = parameterNames.nextElement()
            val value = request.getParameter(name)
            Parameters[name] = value
        }
        ResponseStatus = response.status
        Proto = request.protocol
        Scheme = request.scheme
        Cookies = request.cookies
    }
}