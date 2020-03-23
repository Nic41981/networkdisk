//兼容IE
if (!window.location.origin) {
    window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port: '');
}
$(function () {
    //输入框
    $("#input-email").textbox({
        prompt: "邮箱",
        required: true,
        validType:['email','length[5,50]'],
        validateOnCreate: false,
        tipPosition: "left"
    });
    $("#input-nickname").textbox({
        prompt: "昵称",
        validType:['length[0,20]'],
        validateOnCreate: false,
        tipPosition: "left"
    });
    $("#input-password").passwordbox({
        prompt: "密码",
        required: true,
        validType: ['length[5,20]'],
        validateOnCreate: false,
        tipPosition: "left"
    });
    $("#input-invitation").textbox({
        prompt: "邀请码",
        //TODO 远程验证邀请码
        validType:["remote['http://" + window.location.host + "/verification','code']"],
        validateOnCreate: false,
        tipPosition: "left",
        invalidMessage: "邀请码错误！"
    });
    $("#input-verification").textbox({
        prompt: "验证码",
        required: true,
        validType:["remote['" + window.location.origin + "/verification','code']"],
        validateOnCreate: false,
        tipPosition: "left",
        invalidMessage: "验证码错误！"
    });
    //验证码图片
    $("#vc-img").click(function () {
        $(this).attr("src", "/verification?");
    });
    //帮助按钮
    $("#login-account-btn").tooltip({
        position: "right",
        content: "登录已有账号"
    });
    $("#change-vc-btn").tooltip({
        position: "right",
        content: "换一张"
    }).click(function () {
        $("#vc-img").attr("src", "/verification?");
    });
    //域名信息
    $("#input-host").val(window.location.origin);
    //注册
    $("#register-btn").linkbutton({
        text: "注册",
        onClick: function () {
            let email = $("#input-email");
            let nickname = $("#input-nickname");
            let password = $("#input-password");
            let invitation = $("#input-invitation");
            let verification = $("#input-verification");
            let form = $("#register-form");
            if (
                email.textbox('isValid') &&
                nickname.passwordbox('isValid') &&
                password.textbox('isValid') &&
                verification.textbox('isValid')
            ){
                if (invitation.length > 0){
                    if (!invitation.textbox("isValid")){
                        return false;
                    }
                }
                let host_input = ("<input name='host' type='hidden' value='" + window.location.origin +"'/>");
                form.append(host_input);
                form.submit();
            }
        }
    });
});