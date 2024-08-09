package com.xayah.libsardine.model

import com.xayah.libsardine.util.KotlinSardineUtil
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName(value = "propname", namespace = KotlinSardineUtil.DEFAULT_NAMESPACE_URI, prefix = KotlinSardineUtil.DEFAULT_NAMESPACE_PREFIX)
data class Propname(
    @XmlValue
    var value: String? = null,
)