package com.dy.networkdisk.api.file

import com.dy.networkdisk.api.dto.QYResult
import com.dy.networkdisk.api.dto.file.*

interface FileHomeService {
    fun getRootID(userID: Long): Long?

    fun getChildren(sessionID: Long, userID: Long, nodeID: Long): QYResult<List<FileNodeResult>>

    fun getFolderTreeChildren(sessionID: Long, userID: Long, nodeID: Long): QYResult<Map<Long, String>>

    fun createFolder(dto: CreateFolderDTO): QYResult<Unit>

    fun rename(dto: RenameDTO): QYResult<Unit>

    fun delete(dto: DeleteDTO): QYResult<Unit>

    fun createFile(dto: CreateFileDTO): QYResult<Unit>
}