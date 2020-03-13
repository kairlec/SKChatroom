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

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val error = RequestAuthenticator.authHttpServletRequest(request, blackAPIList)
        return if (error.ok()) {
            true
        } else {
            response.writer.write(ResponseDataUtils.error(error).toString())
            false
        }
    }

    companion object {
        private val blackAPIList = arrayOf("/api/user/login")
        val pathPatterns: List<String> = ArrayList(listOf(
                "/api/user/**"
        ))
    }
}