package cn.skstudio.`interface`

import cn.skstudio.local.utils.LocalConfig.Companion.toJSON
import com.fasterxml.jackson.annotation.JsonIgnore

interface ResponseDataInterface {
    val code: Int
    val msg: String
    val data: Any?

    val json
        @JsonIgnore
        get() = String.toJSON(this)
}