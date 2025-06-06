/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.servers.data.interactor

import com.blacksquircle.ui.core.database.dao.server.ServerDao
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.servers.api.interactor.ServerInteractor
import com.blacksquircle.ui.feature.servers.data.cache.ServerCredentials
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ServerInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val serverDao: ServerDao,
) : ServerInteractor {

    override fun flowAll(): Flow<List<ServerConfig>> {
        return serverDao.flowAll().map { servers ->
            servers.map(ServerMapper::toModel)
        }
    }

    override suspend fun authenticate(uuid: String, credentials: String) {
        ServerCredentials.put(uuid, credentials)
    }

    override suspend fun loadServer(uuid: String): ServerConfig {
        return withContext(dispatcherProvider.io()) {
            val serverEntity = serverDao.load(uuid)
            val serverConfig = ServerMapper.toModel(serverEntity)
            when (serverConfig.authMethod) {
                AuthMethod.PASSWORD -> serverConfig.copy(
                    password = ServerCredentials.get(uuid)
                        ?: serverConfig.password
                )
                AuthMethod.KEY -> serverConfig.copy(
                    passphrase = ServerCredentials.get(uuid)
                        ?: serverConfig.passphrase
                )
            }
        }
    }
}