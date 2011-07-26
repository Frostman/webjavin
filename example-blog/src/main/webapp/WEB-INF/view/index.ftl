[#ftl]
[#import "base.ftl" as base]
[@base.layout]
Hello, World!

<div id="dialog" title="dialog title">
    Dialog Content
</div>

<input type="button" value="Show Dialog" onclick="$('#dialog').wijdialog('open')"/>

<script type="text/javascript">
    $(function() {
        $("#dialog").wijdialog({
            autoOpen: false,
            modal: true,
            captionButtons: {
                pin: { visible: false },
                refresh: { visible: false },
                toggle: { visible: false },
                minimize: { visible: false }
            }
        });
    });
</script>

[/@base.layout]
