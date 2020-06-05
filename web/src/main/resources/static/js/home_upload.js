/*****文件上传*****/
let state = 'pending';
let win,file_list,uploader;

let taskMap = {};
let hasSkip = [];

//变量初始化
$(function () {
    win = $("#uploader");
    file_list = $("#fileList");
    uploader = WebUploader.create({
        swf: '../webuploader/Uploader.swf',
        server: '/upload/chunk',
        formData: {
            task: 0
        },
        pick: '#upload-btn',
        resize: false,
        chunked: true,
        chunkSize: 1024 * 1024, //分片大小1M
        chunkRetry: 3, //分片重试3次
        threads: 5, //线程数5
        fileSizeLimit: 2 * 1024 * 1024 * 1024,
        fileSingleSizeLimit: 1024 * 1024 * 1024
    });
    uploader.addButton('#add-file-btn')
});

//UI初始化
$(function () {
    //上传窗口
    win.window({
        title: '文件上传',
        width: '40%',
        height: '60%',
        modal: true,
        onClose: onWindowClose,
        closed: true
    });
    $("#control-upload-btn").linkbutton({
        title: '开始上传',
        onClick: function () {
            uploader.upload()
        }
    })
});

//监听
WebUploader.Uploader.register({
    "before-send-file":"beforeSendFile"
},{
    beforeSendFile: function (file) {
        $.ajax({
            url: "/upload/before",
            async: false,
            data: {
                parent: current_parent,
                name: file.name,
                mime: file.type,
                size: file.size
            },
            dataType: "json",
            success: function (data) {
                if (data.status === true){
                    taskMap[file.id] = data.data;
                }
                else {
                    uploader.skipFile(file);
                    onUploadError(data.msg);
                    hasSkip.push(file.id);
                }
            }
        });
    }
});

//事件
$(function () {
    uploader.on('fileQueued',onFileQueued);
    uploader.on('uploadBeforeSend',onUploadBeforeSend);
    uploader.on('uploadProgress',onUploadProgress);
    uploader.on('uploadSuccess', onUploadSuccess);
    uploader.on('uploadError', onUploadError);
});

//窗口关闭
function onWindowClose() {
    for (let file of uploader.getFiles()) {
        uploader.removeFile(file);
        $("#" + file.id).remove()
    }
    hasSkip = [];
    taskMap = {};
}

//添加文件
function onFileQueued(file){
    win.window('open');
    let box = $('<div>',{
        id: file.id
    });
    //标题行
    let title = $('<div>',{
        text: file.name,
        class: 'file_list_title'
    });
    box.append(title);
    //状态行
    let status = $('<div>',{
        text: '等待上传',
        class: 'file_list_status'
    });
    box.append(status);
    //进度行
    let progress = $('<div>',{
        class: 'easyui-progressbar'
    });
    box.append(progress);
    //组装
    file_list.append(box);
    $.parser.parse(box);
//    uploader.md5File(file)
//        .then(
//            function(val) {
//                uploader.options.formData.md5 = val;
//                status.text("等待上传");
//            }
//        )
}

//上传分片前
function onUploadBeforeSend(block,data) {
    let file = block.file;
    let id = file.id;
    if (hasSkip.indexOf(id) !== -1){
        //已跳过的文件不更新状态
        return;
    }
    let task = taskMap[id];
    if (!task){
        //没有task信息,
        uploader.skipFile(file);
        onUploadError(file,"缺少任务信息！");
        hasSkip.push(file.id);
        return;
    }
    data.task = task;
}

//上传进度
function onUploadProgress(file,percentage) {
    if (hasSkip.indexOf(file.id) !== -1){
        //已跳过的文件不更新状态
        return
    }
    let progressBar = $("#" + file.id).find(".easyui-progressbar");
    let value = progressBar.progressbar('getValue');
    if (value <= percentage * 100){
        progressBar.progressbar("setValue",(percentage * 100).toFixed(2))
    }
}

//上传成功
function onUploadSuccess(file) {
    if (hasSkip.indexOf(file.id) !== -1){
        //已跳过的文件不更新状态
        return
    }
    $("#" + file.id).find(".file_list_status").text("上传成功")
}

//上传失败
function onUploadError(file,reason) {
    if (hasSkip.indexOf(file.id) !== -1){
        //已跳过的文件不更新状态
        return
    }
    $("#" + file.id).find(".file_list_status").text("上传失败(" + reason + ")")
}

//上传结束
function onUploadComplete(file) {
}