package cn.skstudio.utils

import javax.servlet.http.HttpServletRequest

// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
val HttpServletRequest.IP: String
    get() {
        var ip = this.getHeader("X-Forwarded-For")
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = this.getHeader("Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = this.getHeader("WL-Proxy-Client-IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = this.getHeader("HTTP_CLIENT_IP")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = this.getHeader("HTTP_X_FORWARDED_FOR")
            }
            if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
                ip = this.remoteAddr
            }
        } else if (ip.length > 15) {
            ip.split(",").toTypedArray().forEach {
                if (!"unknown".equals(it, ignoreCase = true)) {
                    return it
                }
            }
        }
        return ip
    }
