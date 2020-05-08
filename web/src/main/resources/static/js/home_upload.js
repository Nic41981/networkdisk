/*****文件上传*****/
let state = 'pending';
let win,file_list,uploader;

//变量初始化
$(function () {
    win = $("#uploader");
    file_list = $("#fileList");
    uploader = WebUploader.create({
        swf: '../webuploader/Uploader.swf',
        server: '/upload',
        formData: {
            md5: '',
            sha256: ''
        },
        pick: '#upload-btn',
        resize: false,
        chunked: true,
        chunkSize: 1024 * 1024, //分片大小1M
        chunkRetry: 3, //分片重试3次
        threads: 5 //线程数5
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

//事件
$(function () {
    uploader.on('fileQueued',onFileQueued);
    // uploader.on('uploadBeforeSend',onUploadBeforeSend);
    uploader.on('uploadSuccess', onUploadSuccess);
    uploader.on('uploadError', onUploadError);
    uploader.on('uploadComplete', onUploadComplete)
});

//窗口关闭
function onWindowClose() {
    if (state === 'pending'){
        for (let file of uploader.getFiles()){
            uploader.removeFile(file);
            $("#" + file.id).remove()
        }
    }
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
        text: '上传准备中',
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
    uploader.md5File(file)
        .then(
            function(val) {
                uploader.options.formData.md5 = val;
                status.text("等待上传");
            }
        )
}

//分片上传
function onUploadBeforeSend(block,data){
    let reader = new FileReader();
    reader.readAsArrayBuffer(block.blob.source);
    reader.onload = function(){
        let wordArray = CryptoJS.lib.WordArray.create(reader.result);
        data.options.formData.test = 123
        // data.formData.sha256 = CryptoJS.SHA256(wordArray).toString();
    };
    uploader.md5File(block.blob).then(
        function (val) {
            console.log(val)
        }
    )
}

//上传成功
function onUploadSuccess(file) {
    $("#" + file.id).find(".file_list_status").text("上传成功")
}

//上传失败
function onUploadError(file) {
    $("#" + file.id).find(".file_list_status").text("上传失败")
}

//上传结束
function onUploadComplete(file) {
}