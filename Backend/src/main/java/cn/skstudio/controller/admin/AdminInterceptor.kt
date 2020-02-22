package cn.skstudio.controller.admin

/**
 * @author: Kairlec
 * @version: 1.1
 * @description: 管理员接口拦截器
 */
import cn.skstudio.local.utils.RequestAuthenticator
import cn.skstudio.local.utils.ResponseDataUtils
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AdminInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val error = RequestAuthenticator.authHttpServletRequest(request, blackAPIList)
        return if (error.ok()) {
            //在请求验证器中的成功data会返回用户身份
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
        val pathPatterns: List<String> = ArrayList(listOf(
                "/api/admin",
                "/api/admin/*"
        ))
    }
}