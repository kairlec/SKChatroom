package cn.skstudio.intf

import cn.skstudio.utils.toJSON
import com.fasterxml.jackson.annotation.JsonIgnore

interface ResponseDataInterface {
    val code: Int
    val msg: String
    val data: Any?

    val json
        @JsonIgnore
        get() = String.toJSON(this)
}