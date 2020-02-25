package cn.skstudio.controller.user

import cn.skstudio.local.utils.RequestAuthenticator
import cn.skstudio.local.utils.ResponseDataUtils
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserInterceptor : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val error = RequestAuthenticator.authHttpServletRequest(request, blackAPIList)
        return if (error.ok()) {
            true
        } else {
            response.writer.write(ResponseDataUtils.Error(error))
            false
        }
    }

    companion object {
        private val blackAPIList: LinkedList<String> = object : LinkedList<String>() {
            init {
                add("/api/user/login")
            }
        }
        val pathPatterns: List<String> = ArrayList(listOf(
                "/api/user/**"
        ))
    }
}