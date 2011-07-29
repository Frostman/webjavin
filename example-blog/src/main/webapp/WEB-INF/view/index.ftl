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

    var BlogPost = function(title, content) {
        this.title = ko.observable(title);
        this.content = ko.observable(content);
    };

    var viewModel = {
    };

    viewModel.blogPosts = ko.observableArray([
        ko.observable(new BlogPost("title1", "content1")),
        ko.observable(new BlogPost("title2", "content2")),
        ko.observable(new BlogPost("title3", "content3")),
        ko.observable(new BlogPost("title4", "content4"))
    ]);

    $(function() {
        ko.applyBindings(viewModel);

        var idx = 2;
        setInterval(function() {
            viewModel.blogPosts()[0].content = ko.observable("content" + idx);
//            alert(viewModel.blogPosts()[0].content);
//            viewModel.blogPosts.push(new BlogPost("title", "content" + idx));
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
