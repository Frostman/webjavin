[#ftl]
[#macro layout title='Blog' head='']
<!DOCTYPE HTML>
<html>
<head>
    <title>${title!}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

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
