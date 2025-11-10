package com.grupo3.config

import org.springframework.graphql.server.WebGraphQlInterceptor
import org.springframework.graphql.server.WebGraphQlRequest
import org.springframework.graphql.server.WebGraphQlResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import reactor.core.publisher.Mono

@Component
class GraphQlContextInterceptor : WebGraphQlInterceptor {
    override fun intercept(
        request: WebGraphQlRequest,
        chain: WebGraphQlInterceptor.Chain
    ): Mono<WebGraphQlResponse> {
        val servletAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val httpServletRequest = servletAttributes?.request
        val httpServletResponse = servletAttributes?.response
        val authentication = SecurityContextHolder.getContext()?.authentication

        request.configureExecutionInput { executionInput, builder ->
            builder.graphQLContext { contextBuilder ->
                httpServletRequest?.let { contextBuilder.put("request", it) }
                httpServletResponse?.let { contextBuilder.put("response", it) }
                authentication?.let { contextBuilder.put("authentication", it) }
            }
            builder.build()
        }

        return chain.next(request)
    }
}

