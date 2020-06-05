//主目录表格结构
var main_table_struct = [[
    {field: 'id',hidden: true},
    {field: 'type',hidden: true},
    {field: 'order',hidden:true, sortable:true, order: 'asc'},
    {field: 'check',checkbox: true},
    {field: 'name',title:'文件名',width: '40%',resizable: false,sortable: true},
    {field: 'status',title:'状态',width: '19%',resizable: false,sortable: true},
    {field: 'size',title: '大小',width: '20%',resizable: false,sortable: true},
    {field: 'createTime',title: '上传时间',width: '20%',resizable: false,sortable: true}
]];

var menuClickRow;

//UI初始化
$(function () {
    //折叠面板
    $("#navigation").accordion({
        fit: true,
        border: false
    });
    //折叠面板-搜索
    $("#search").searchbox({
        prompt: "搜索文件",
        searcher: onSearch
    });
    //折叠面板-目录树
    $("#folder-tree").tree({
        url: '/file/folderTree.json',
        method: 'get'
    });
    //主目录
    $("#main-table").datagrid({
        toolbar: '#toolbar',
        footer: '#footer',
        fit: true,
        url: current_node.remote,
        method: 'get',
        queryParams: current_node.params,
        columns: main_table_struct,
        onRowContextMenu: onContextMenu,
        onDblClickRow: onOpenDir
    });
    //主目录-根目录按钮
    $("#root-btn").linkbutton({
        onClick: toRoot
    });
    //主目录-上一级按钮
    $("#parent-btn").linkbutton({
        onClick: toParent
    });
    //主目录-新建文件夹
    $("#create-folder-btn").linkbutton({
        onClick: onCreateFolder
    });
    //主目录-右键菜单
    $("#context-menu").menu({
        onHide: function () {
            $("#context-menu").hide()
        },
        onShow: function () {
            $("#context-menu").show();
        },
        onClick: onContextMenuClick
    }).hide();
});

/**
 * 搜索
 * @param value 关键词
 */
function onSearch(value) {
    //TODO 文件搜索
}

/**
 * 跳转至根目录
 */
function toRoot() {
    let tmp = JSON.stringify(current_node);
    node_stack.push(JSON.parse(tmp));
    tmp = JSON.stringify(root_node);
    current_node = JSON.parse(tmp);
    current_parent = root_id;
    $("#main-table").datagrid('load');
}

/**
 * 返回上级目录
 */
function toParent() {
    let target_node = node_stack.pop();
    if (!target_node){
        $.messager.alert('提示','当前目录已经是根目录。','info');
        return
    }
    current_node = target_node;
    $("#main-table").datagrid('load');
}

/**
 * 主目录右键菜单
 * @param e
 * @param index
 * @param row
 */
function onContextMenu(e,index,row) {
    e.preventDefault();
    let menu = $("#context-menu");
    if (!row){
        menu.menu('disableItem',$("#open_menu_item"));
        menu.menu('disableItem',$("#download_menu_item"));
        menu.menu('disableItem',$("#copy_menu_item"));
        menu.menu('disableItem',$("#cut_menu_item"));
        menu.menu('disableItem',$("#rename_menu_item"));
        menu.menu('disableItem',$("#delete_menu_item"));
    }
    else {
        if (row.type === 'FOLDER'){
            menu.menu('disableItem',$("#download_menu_item"));
            menu.menu('enableItem',$("#open_menu_item"));
        }
        else {
            menu.menu('disableItem',$("#open_menu_item"));
            menu.menu('enableItem',$("#download_menu_item"));
        }
        menu.menu('enableItem',$("#copy_menu_item"));
        menu.menu('enableItem',$("#cut_menu_item"));
        menu.menu('enableItem',$("#rename_menu_item"));
        menu.menu('enableItem',$("#delete_menu_item"));
    }
    if (!clipboard){
        menu.menu('disableItem',$("#paste_menu_item"));
    }
    else {
        menu.menu('enableItem',$("#paste_menu_item"));
    }
    menuClickRow = row;
    menu.menu('show',{left: e.pageX,top: e.pageY})
}

/**
 * 主目录右键菜单点击事件
 */
function onContextMenuClick(item) {
    let row = menuClickRow;
    switch (item.id) {
        case 'reload_menu_item': {
            $("#main-table").datagrid('load');
            break
        }
        case 'open_menu_item': {
            onOpenDir(row);
            break
        }
        case 'download_menu_item': {
            onDownload(row);
            break
        }
        case 'copy_menu_item': {
            clipOperation(false,row);
            break
        }
        case 'cut_menu_item': {
            clipOperation(true,row);
            break
        }
        case 'paste_menu_item': {
            break
        }
        case 'rename_menu_item': {
            onRename(row);
            break
        }
        case 'delete_menu_item': {
            onDelete(row);
            break
        }
        default: {
        }
    }
}

/**
 * 下载
 */
function onDownload(row) {
    $.ajax({
        url: "/download",
        async: true,
        data: {
            id: row.id
        },
        type: "GET"
    })
}

/**
 * 打开文件夹
 */
function onOpenDir(row) {
    if (row.type === 'FOLDER'){
        let tmp = JSON.stringify(current_node);
        let tmpObj = JSON.parse(tmp);
        node_stack.push(tmpObj);
        current_node = {
            path: tmpObj.path + "/" + row.name,
            remote: '/file/children.json',
            params: {
                id: row.id
            }
        };
        current_parent = row.id;
        $("#main-table").datagrid('load')
    }
}

/**
 * 复制/剪切
 */
function clipOperation(isCut,row) {
    let selectedRows = $("#main-table").datagrid('getSelections');
    let items = [];
    if (selectedRows instanceof Array){
        selectedRows.forEach((item)=>{
            if (item.hasOwnProperty('id')){
                items.push(item.id)
            }
        })
    }
    if (items.length === 0){
        items.push(row.id)
    }
    clipboard = {
        isCut: isCut,
        items: items
    }
}

/**
 * 新建目录
 */
function onCreateFolder() {
    $.messager.prompt('新建文件夹', '请输入新文件夹名', function (value) {
        if (!value) {
            return
        }
        $.ajax({
            url: '/file/createFolder',
            async: false,
            type: 'POST',
            data: {
                parent: current_parent,
                name: value
            },
            success: function (result) {
                let obj = JSON.parse(result);
                if (obj.status){
                    $("#main-table").datagrid('load');
                }
                else {
                    $.messager.alert('创建失败',obj.msg,'error');
                }
            }
        });
    })
}

/**
 * 重命名
 */
function onRename(row) {
    $.messager.prompt('重命名','请输入新的名字：',function (value) {
        if (!value) {
            return
        }
        $.ajax({
            url: '/file/rename',
            async: false,
            type: 'POST',
            data: {
                parent: current_parent,
                id:row.id,
                type:row.type,
                name: value
            },
            success: function (result) {
                let obj = JSON.parse(result);
                if (obj.status) {
                    $("#main-table").datagrid('load')
                }
                else {
                    $.messager.alert('重命名失败',obj.msg,'error');
                }
            }
        })
    });
}

/**
 * 删除
 */
function onDelete(row) {
    $.messager.prompt("确认删除","请输入您要删除的文件/文件夹名称以确认删除：",function (value) {
        if (!value){
            return;
        }
        if (value !== row.name.replace(/<[^>]+>/g,"")){
            console.log(value + "--" + row.name);
            $.messager.alert('取消删除','您的输入与目标不符，本次删除已经取消','info');
            return
        }
        $.ajax({
            url: '/file/delete',
            async: false,
            type: 'POST',
            data: {
                parent: current_parent,
                id: row.id,
                type: row.type
            },
            success: function (result) {
                let obj = JSON.parse(result);
                if (obj.status) {
                    $("#main-table").datagrid('load')
                }
                else {
                    $.messager.alert('删除失败',obj.msg,'error');
                }
            }
        })
    })
}