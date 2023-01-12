package com.quico.tech.model



data class CategoryDetailResponse(
    val id: Any,
    val jsonrpc: String,
    val result: CategoryDetail?,
    val error: String?
    //val error: ErrorData?
)

data class CategoryDetail(
    val subcategories: List<SubCategory>?,
    val products: List<Product>?,
    val pagination: PaginationCategoryDetail?
)

data class PaginationCategoryDetail(
    val page: Int,
    val size: Int,
    val total: Int
)

data class PaginationBodyParameters(
    val params: PaginationParams
)

data class PaginationParams(
    val page: Int
)