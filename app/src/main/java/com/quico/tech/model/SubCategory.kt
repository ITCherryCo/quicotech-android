package com.quico.tech.model

data class SubCategory(
    val id: Int,
    val name: String,
    val image: String,
)


data class SubCategoryResponse(
    val id: Any,
    val jsonrpc: String,
    val result: ProductBySubCategory?,
    val error: String?
    //val error: ErrorData?
)

data class ProductBySubCategory(
    val products: List<Product>?,
    val pagination: PaginationProductBySubCategory?
)

data class PaginationProductBySubCategory(
    val page: Int,
    val size: Int,
    val total_pages: Int
)

data class PaginationProductBySubCategoryBodyParameters(
    val params: PaginationProductBySubCategoryParams
)

data class PaginationProductBySubCategoryParams(
    val page: Int,
    val subcategory_id: Int
)