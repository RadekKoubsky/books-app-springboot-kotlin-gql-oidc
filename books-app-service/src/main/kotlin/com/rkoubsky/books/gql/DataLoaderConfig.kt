package com.rkoubsky.books.gql

import com.rkoubsky.books.service.model.Author
import org.dataloader.BatchLoaderEnvironment
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.BatchLoaderRegistry
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class DataLoaderConfig(
    private val registry: BatchLoaderRegistry,
    private val authorDataLoader: AuthorDataLoader
) {

    init {
        registry.forTypePair(UUID::class.java, Author::class.java).registerMappedBatchLoader({ authorIds: Set<UUID>, env: BatchLoaderEnvironment ->
            Mono.just(authorDataLoader.load(authorIds))
        })
    }
}