$.extend($.fn.validatebox.defaults.rules,{
    username: {
        validator : function (value) {
            const regUsername = /^[\w\u4e00-\u9fa5]+$/;
            return regUsername.test(value)
        },
        message: '合法字符：数字、字母、下划线、汉字'
    }
});

$(function () {
    $("#input-username").textbox({
        prompt: "用户名",
        required: true,
        validType:['length[2,20]','username'],
        validateOnCreate: false
    });
    $("#login-account-btn").tooltip({
        position: "right",
        content: "登录已有账号"
    });
    $("#input-password").passwordbox({
        prompt: "密码",
        required: true,
        validType: ['length[5,20]'],
        validateOnCreate: false
    });
    $("#input-email").textbox({
        prompt: "邮箱",
        required: true,
        validType:['email'],
        validateOnCreate: false
    });
    $("#input-verification").textbox({
        prompt: "验证码",
        required: true,
        validateOnCreate: false
    });
    $("#change-vc-btn").tooltip({
        position: "right",
        content: "换一张"
    }).click(function () {
        $("#vc-img").attr("src", "/verification?");
    });
    $("#vc-img").click(function () {
        $(this).attr("src", "/verification?");
    });

    $("#register-btn").linkbutton({
        text: "注册",
        onClick: function () {
            if ($("#input-username").textbox('isValid') &&
                $("#input-password").passwordbox('isValid') &&
                $("#input-email").textbox('isValid') &&
                $("#input-verification").textbox('isValid')
            ){
                $("#register-form").submit();
            }
        }
    });
});