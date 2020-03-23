$(function () {
    //输入框
    $("#input-email").textbox({
        prompt: "邮箱",
        required: true,
        validType:['email'],
        validateOnCreate: false,
        tipPosition: "left",
    });
    $("#input-password").passwordbox({
        prompt: "密码",
        required: true,
        validateOnCreate: false,
        tipPosition: "left",
    });
    $("#input-verification").textbox({
        prompt: "验证码",
        required: true,
        validType: ["remote['http://" + window.location.host + "/verification','code']"],
        validateOnCreate: false,
        tipPosition: "left",
        invalidMessage: "验证码错误！"
    });
    $("#input-auto-login").checkbox({
        label: "自动登录",
        labelPosition: "after"
    });
    //验证码图片
    $("#vc-img").click(function () {
        $(this).attr("src", "/verification?");
    });
    //帮助按钮
    $("#create-account-btn").tooltip({
        position: "right",
        content: "注册新用户"
    });
    $("#forget-password-btn").tooltip({
        position: "right",
        content: "找回密码"
    });
    $("#change-vc-btn").tooltip({
        position: "right",
        content: "换一张"
    }).click(function () {
        $("#vc-img").attr("src", "/verification?");
    });
    //登录
    $("#login-btn").linkbutton({
        text: "登录",
        onClick: function () {
            let email = $("#input-email");
            let password = $("#input-password");
            let verification = $("#input-verification");
            let form = $("#login-form");
            if (
                email.textbox("isValid") &&
                password.passwordbox("isValid") &&
                verification.textbox("isValid")
            ) {
                form.submit()
            }
        }
    });
});
