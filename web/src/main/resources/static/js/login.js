$(function () {
    $("#input-username").textbox({
        prompt: "用户名",
        required: true,
        validateOnCreate: false
    });
    $("#create-account-btn").tooltip({
        position: "right",
        content: "注册新用户"
    });

    $("#input-password").passwordbox({
        prompt: "密码",
        required: true,
        validateOnCreate: false
    });
    $("#forget-password-btn").tooltip({
        position: "right",
        content: "找回密码"
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
    $("#input-auto-login").checkbox({
        label: "自动登录",
        labelPosition: "after"
    });
    $("#login-btn").linkbutton({
        text: "登录",
        onClick: function () {
            if (
                $("#input-username").textbox("isValid") &&
                $("#input-password").passwordbox("isValid") &&
                $("#input-verification").textbox("isValid")
            ){
                $("#login-form").submit()
            }
        }
    });
});
