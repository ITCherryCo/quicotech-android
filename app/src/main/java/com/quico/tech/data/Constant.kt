package com.quico.tech.data

import com.quico.tech.model.Address
import com.quico.tech.model.RegisterParams
import com.quico.tech.model.User

object Constant {
    var WEB_BASE_URL = "http://13.39.86.18:8069/api/"
    var ERROR = "error"
    var EXCEPTION = "EXCEPTION"
    var PHONE_NUMBER = "phone_number"
    val VERIFICATION_TYPE = "verification_type" // email link or phone number verification
    var OPERATION_TYPE = "operation_type"       // register new account or change phone number
    val EMAIL_LINK = "email_link"       // register new account or change phone number
    val ORDERS_TAG = "ORDERS_RESPONSE"
    val ORDERS_TYPE= "orders_type"
    val ONGOING_ORDERS= "ongoing_orders"
    val DONE_ORDERS= "done_orders"
    val DELIVERED= "Delivered"
    val TRACK_ORDER= "Track Order"
    val CANCELED= "Canceled"
    val ALL= "all"
    val ORDERS= "orders"
    val SERVICES= "services"
    val CONNECTION= "connection"
    val NO_ITEM= "no_item"
    val EN= "en"
    val AR= "ar"
    val DOOR_TO_DOOR= "door_to_door"
    val DROP_CENTER= "drop_center"
    var CHECKOUT_TYPE= "checkout_type"
    var SERVICE= "service"
    var TRACKING_ON= "tracking_on"
    var PROFILE_EDIT_TYPE= "profile_edit_type"
    var REGISTER= "register"
    var CHANGE_PHONE_NUMBER= "change_phone_number"
    var CHANGE_EMAIL= "change_email"
    var CHANGE_PASSWORD= "change_password"
    var FORGET_PASSWORD= "forget_password"
    var USER_REGISTER_TAG= "USER_REGISTER_TAG"
    var USER_LOGIN_TAG= "USER_LOGIN_TAG"
    val ADDRESS_TAG= "ADDRESS_TAG"
    val SERVICE_TAG= "SERVICE_TAG"
    val PRODUCT_TAG= "PRODUCT_TAG"
    val ADDRESS= "address"
    val EMAIL= "email"
    val NO_ADDRESSES= "no_addresses"
    val NO_BRANDS= "no_brands"
    val NO_Categories= "no_categories"
    val NO_ORDERS= "no_orders"
    val SERVICE_ID= "service_id"
    val NO_SERVICES= "no_services"
    val NO_ITEMS= "no_items"
    val EMPTY_SEARCH= "empty_search"
    val SUCCESS= "success"
    val USER_UPDATE_TAG= "USER_UPDATE_TAG"
    val USER_LOGOUT_TAG= "USER_LOGON_TAG"
    val COOKIE= "Cookie"
    val SESSION_ID= "session_id"
    var CATEGORY_TAG= "CATEGORY_TAG"
    var BRAND_TAG= "BRAND_TAG"
    var CONTENT_TYPE= "Content-Type"
    var APPLICATION_JSON= "application/json"
    var can_register= false
    val EMPTY_CART= "empty_cart"
    val NO_CARDS= "no_cards"
    val APP_MEDIA_PATH= "/QuicoTech/Media/Recording"
    var PASSWORD= "password"
    val PRODUCT_ID= "product_id"
    val PRODUCT_NAME= "product_name"
    val CATEGORY_ID= "category_id"
    val BRAND_ID= "BRAND_id"
    var ITEM_ID= "item_id"
    var SEND_EMAIL_LINK= "SEND_EMAIL_LINK"
    var TEMPORAR_USER : RegisterParams? =null
    var TEMPORAR_ADDRESS : Address? =null
    var COOKIES_KEY ="cookiesKey"
    var CREDENTIAL_OPERATION_TYPE =""
    var ACTIVITY_TYPE ="activity_type"
    var TERMS_OF_USE ="terms_of_use"
    var VIP_BENEFITS ="vip_benefits"



}