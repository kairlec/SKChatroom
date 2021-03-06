package cn.skstudio.interceptor

/**
 * @author: Kairlec
 * @version: 1.1
 * @description: 管理员接口拦截器
 */
import cn.skstudio.local.utils.RequestAuthenticator
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class GroupInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val error = RequestAuthenticator.authHttpServletRequest(request, blackAPIList)
        return if (error.ok) {
            true
        } else {
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write(error.json)
            false
        }
    }

    companion object {
        private val blackAPIList= emptyArray<String>()
        val pathPatterns: List<String> = ArrayList(listOf(
                "/api/group/**"
        ))
    }
}