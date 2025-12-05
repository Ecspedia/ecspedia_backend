package com.grupo3.agent
import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.model.PromptExecutor


class HotelAgentStrategy(
    private val toolRegistry: ToolRegistry,
    private val promptExecutor: PromptExecutor
) {
    fun createHotelAgent() {
        val strategy =  strategy<String, String>("hotel-assistant"){
            // Node 1: Understand user intent

        }

    }
}