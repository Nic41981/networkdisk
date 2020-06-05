package com.dy.networkdisk.web.controller.file

import com.dy.networkdisk.api.config.FileConst
import com.dy.networkdisk.api.dto.file.CreateFolderDTO
import com.dy.networkdisk.api.dto.file.DeleteDTO
import com.dy.networkdisk.api.dto.file.RenameDTO
import com.dy.networkdisk.api.file.FileWebService
import com.dy.networkdisk.web.tool.sessionID
import com.dy.networkdisk.web.tool.sessionInfo
import com.dy.networkdisk.web.tool.toJson
import com.dy.networkdisk.web.vo.*
import com.dy.networkdisk.web.vo.file.DeleteVO
import com.dy.networkdisk.web.vo.file.FolderTreeJsonVO
import com.dy.networkdisk.web.vo.file.MainListJsonVO
import com.dy.networkdisk.web.vo.file.RenameVO
import org.apache.dubbo.config.annotation.Reference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/file")
class FileHomeController @Autowired constructor(
        val template: StringRedisTemplate
) {

    @Reference
    private lateinit var service: FileWebService

    @GetMapping("/folderTree.json")
    @ResponseBody
    fun getFolderTreeJson(request: HttpServletRequest,id: String?): String{
        val mid: Long = if (id.isNullOrBlank()){
            //根节点
            0L
        }
        else {
            //如果节点id异常返回空列表
            id.toLongOrNull(16) ?: return "[]"
        }
        val result = service.getChildrenFolderTree(request.sessionID,request.sessionInfo.id,mid)
        if (result.isSuccess){
            val resultList = ArrayList<FolderTreeJsonVO>()
            for ((k,v) in result.data!!){
                resultList.add(FolderTreeJsonVO(
                        id = k.toString(16),
                        text = v
                ))
            }
            return resultList.toJson()
        }
        return "[]"
    }

    @GetMapping("/children.json")
    @ResponseBody
    fun getMainListJson(request: HttpServletRequest, id: String): String{
        //如果节点id异常返回空列表
        val mid = id.toLongOrNull(16) ?: return "[]"
        val result = service.getChildren(request.sessionID,request.sessionInfo.id,mid)
        if (result.isSuccess){
            val resultList = ArrayList<MainListJsonVO>()
            for (it in result.data!!){
                resultList.add(MainListJsonVO(
                        id = it.id.toString(16),
                        type = it.type,
                        order = it.order,
                        name = "<img class='${it.type.toLowerCase()}'/>${it.name}",
                        status = FileConst.Status.valueOf(it.status).tip,
                        size = it.size,
                        createTime = it.createTime
                ))
            }
            return resultList.toJson()
        }
        return "[]"
    }

    @PostMapping("/createFolder")
    @ResponseBody
    fun createFolder(request: HttpServletRequest,parent: String,name: String): String{
        val mParent = parent.toLongOrNull(16) ?: return ResultJsonVO<Unit>(status = false,msg = "参数异常！").toJson()
        val result = service.createFolder(CreateFolderDTO(
                sessionID = request.sessionID,
                userID = request.sessionInfo.id,
                parent = mParent,
                name = name
        ))
        return ResultJsonVO<Unit>(status = result.isSuccess,msg = result.msg).toJson()
    }

    @PostMapping("/rename")
    @ResponseBody
    fun rename(request: HttpServletRequest,vo: RenameVO): String{
        val mParent = vo.parent.toLongOrNull(16) ?: return ResultJsonVO<Unit>(status = false,msg = "参数异常！").toJson()
        val mid = vo.id.toLongOrNull(16) ?: return ResultJsonVO<Unit>(status = false,msg = "参数异常！").toJson()
        val result = service.rename(RenameDTO(
                sessionID = request.sessionID,
                userID = request.sessionInfo.id,
                parent = mParent,
                id = mid,
                type = vo.type,
                newName = vo.name
        ))
        return ResultJsonVO<Unit>(status = result.isSuccess,msg = result.msg).toJson()
    }

    @PostMapping("/delete")
    @ResponseBody
    fun delete(request: HttpServletRequest,vo: DeleteVO): String{
        val mParent = vo.parent.toLongOrNull(16) ?: return ResultJsonVO<Unit>(status = false,msg = "参数异常！").toJson()
        val mid = vo.id.toLongOrNull(16) ?: return ResultJsonVO<Unit>(status = false,msg = "参数异常！").toJson()
        val result = service.delete(DeleteDTO(
                sessionID = request.sessionID,
                userID = request.sessionInfo.id,
                parent = mParent,
                id = mid,
                type = vo.type
        ))
        return ResultJsonVO<Unit>(status = result.isSuccess,msg = result.msg).toJson()
    }
}