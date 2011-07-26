[#ftl]
[#import "base.ftl" as base]
[@base.layout]
<div>
    Index page of simple blog
    <hr/>
</div>

<div data-bind='template: { name: "postTemplate",
                            foreach: blogPosts }'></div>

<script type="text/html" id="postTemplate">
    [#noparse]
        <button data-bind='text: title, click: function() { alert("clicked:"+ title); }'></button>
        <div data-bind="text: content"></div>
        <hr/>
    [/#noparse]
</script>

<script type="text/javascript">
    var blogPosts = [
        {title: "post1", content: "content1"},
        {title: "post2", content: "content2"},
        {title: "post3", content: "content3"},
        {title: "post4", content: "content4"}
    ];

    var viewModel = {

    };

    $(function() {
        ko.applyBindings(viewModel);

        var idx = 2;
        setInterval(function() {
            blogPosts[0].content = "content" + idx;
            idx++;
        }, 1000);
    });
</script>

[#--<div id="dialog" title="dialog title">
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
</script>--]

[/@base.layout]
