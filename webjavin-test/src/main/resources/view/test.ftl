[#ftl]
[#if page??]
Page: ${page!} <br>
[/#if]

[#if verified??]
Verified: ${verified?string} <br>
[/#if]

test<br>
<br>

[#if version??]
WebJavin version: ${version!} <br>
[/#if]

<br/>

<script type="text/javascript" src="${url('/static/test.js')}"></script>

<iframe src="${url('/async')}">iframes is not supported</iframe>



