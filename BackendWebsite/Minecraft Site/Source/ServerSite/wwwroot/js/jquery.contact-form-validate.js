/* ----------------------------------------------------------- */
/*  Contact Form
/* ----------------------------------------------------------- */
jQuery(document).ready(function($) {
    $("#response .alert").hide();

    $("#contact-form").submit(function() {
        $("#contact-form .form-group").removeClass("has-error");
        $("#response .alert").hide();
        var hasError = false;

        $("#contact-form .requiredField").each(function() {
            if (jQuery.trim($(this).val()) == "") {
                $("#response .alert-danger").show();
                $(this).parent().addClass("has-error");
                hasError = true;
            } else if ($(this).hasClass("email")) {
                var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
                if (!emailReg.test(jQuery.trim($(this).val()))) {
                    $(this).parent().addClass("has-error");
                    hasError = true;
                }
            }
        });

        if (!hasError) {
            var formInput = $(this).serialize();
            $.post($(this).attr("action"),
                formInput,
                function(data) {
                    $("#response .alert-success").show();
                    $("#contact-form")[0].reset();
                });
        }

        return false;

    });
});