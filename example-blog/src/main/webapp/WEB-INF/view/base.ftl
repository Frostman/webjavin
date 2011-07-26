[#ftl]
[#macro layout title='Blog' head='']
<!DOCTYPE HTML>
<html>
<head>
    <title>${title!}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link href="${url('/static/jquery.wijmo-open.1.3.0.css')}" rel="stylesheet">
    <link href="${url('/static/jquery-wijmo.css')}" rel="stylesheet">
    <link href="${url('/static/style.css')}" rel="stylesheet">

    <script type="text/javascript" src="${url('/static/jquery-1.6.2.min.js')}"></script>
    <script type="text/javascript" src="${url('/static/jquery-ui-1.8.14.custom.min.js')}"></script>
    <script type="text/javascript" src="${url('/static/jquery.tmpl.min.js')}"></script>

    <script type="text/javascript" src="${url('/static/jquery.wijmo-open.1.3.0.min.js')}"></script>

    <script type="text/javascript" src="${url('/static/knockout-1.2.1.js')}"></script>

    <script type="text/javascript">

    </script>
${head!}
</head>
<body>

<div id="header">
    [#include "blocks/header.ftl"]
</div>

<div id="main">
    [#nested/]
</div>

<div id="footer">
    [#include "blocks/footer.ftl"]
</div>

</body>
</html>
[/#macro]
