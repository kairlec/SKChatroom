package cn.skstudio.controller.admin

import cn.skstudio.local.utils.RequestAuthenticator
import cn.skstudio.local.utils.ResponseDataUtils
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AdminInterceptor : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val error = RequestAuthenticator.authHttpServletRequest(request, blackAPIList)
        return if (error.ok()) {
            "UserAdmin".equals(error.data as String, true)
        } else {
            response.writer.write(ResponseDataUtils.Error(error))
            false
        }
    }

    companion object {
        private val blackAPIList: LinkedList<String> = object : LinkedList<String>() {
            init {

            }
        }
    }
}