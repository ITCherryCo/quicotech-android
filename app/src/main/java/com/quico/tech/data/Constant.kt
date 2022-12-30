package com.quico.tech.data

import com.quico.tech.model.Address
import com.quico.tech.model.RegisterParams
import com.quico.tech.model.User

object Constant {
    var WEB_BASE_URL = "http://13.39.86.18:8069/api/"
    var ERROR = "error"
    var EXCEPTION = "EXCEPTION"
    var SUCCESS = "success"
    var CONNECTION = "connection"
    var NO_ADDRESSES = "no_addresses"
    var NO_ORDERS = "no_orders"
    var NO_SERVICES = "no_services"
    var NO_Categories = "no_categories"
    var NO_ITEMS = "no_items"
    var SERVICE_ID = "service_id"
    var EMAIL = "email"
    var PHONE_NUMBER = "phone_number"
    var VERIFICATION_TYPE = "verification_type" // email link or phone number verification
    var OPERATION_TYPE = "operation_type"       // register new account or change phone number
    var EMAIL_LINK = "email_link"       // register new account or change phone number
    var ORDERS_TAG = "ORDERS_RESPONSE"
    var ORDERS_TYPE= "orders_type"
    var ONGOING_ORDERS= "ongoing_orders"
    var DONE_ORDERS= "done_orders"
    var DELIVERED= "Delivered"
    var TRACK_ORDER= "Track Order"
    var CANCELED= "Canceled"
    var ALL= "all"
    var ORDERS= "orders"
    var SERVICES= "services"
    var EN= "en"
    var AR= "ar"
    var DOOR_TO_DOOR= "door_to_door"
    var DROP_CENTER= "drop_center"
    var CHECKOUT_TYPE= "checkout_type"
    var SERVICE= "service"
    var TRACKING_ON= "tracking_on"
    var PROFILE_EDIT_TYPE= "profile_edit_type"
    var REGISTER= "register"
    var CHANGE_PHONE_NUMBER= "change_phone_number"
    var CHANGE_EMAIL= "change_email"
    var CHANGE_PASSWORD= "change_password"
    var USER_REGISTER_TAG= "USER_REGISTER_TAG"
    var USER_LOGIN_TAG= "USER_LOGIN_TAG"
    var ADDRESS_TAG= "ADDRESS_TAG"
    var SERVICE_TAG= "SERVICE_TAG"
    var ADDRESS= "address"
    var USER_UPDATE_TAG= "USER_UPDATE_TAG"
    var USER_LOGOUT_TAG= "USER_LOGON_TAG"
    var COOKIE= "Cookie"
    var SESSION_ID= "session_id"
    var CONTENT_TYPE= "Content-Type"
    var APPLICATION_JSON= "application/json"
    var can_register= false
    var EMPTY_CART= "empty_cart"
    var NO_CARDS= "no_cards"
    var APP_MEDIA_PATH= "/QuicoTech/Media/Recording"
    var PASSWORD= "password"
    var PRODUCT_ID= "product_id"
    var CATEGORY_ID= "category_id"
    var BRAND_ID= "BRAND_id"
    var ITEM_ID= "item_id"
    var SEND_EMAIL_LINK= "SEND_EMAIL_LINK"
    var TEMPORAR_USER : User? =null
    var TEMPORAR_ADDRESS : Address? =null


}