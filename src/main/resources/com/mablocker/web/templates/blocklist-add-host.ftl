<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Add Blocklist Items">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
</@components.header>

<@navbar.navbar active="blocklists" />

<div class="container">
    <h1>Please Confirm</h1>

    <p>
        Please confirm that you want to add the following websites to the blocklist ${blocklist.name}.
        <b>You can't undo this!</b>
    </p>

    <ul>
        <#list hosts as host>
            <li><b>${host}</b></li>
        </#list>
    </ul>
    <form class="form-inline" method="post" action="/blocklists/add">
        <input type="hidden" name="blocklist-id" value="${blocklist.id?c}">
        <input type="hidden" name="hosts-base64" value="${hostsBase64}">

        <button type="submit" class="btn btn-success" style="margin-right: 1em;">
            <i class="fa fa-check"></i>
            Add to blocklist "${blocklist.name}"
        </button>
        <a href="#" onclick="window.history.back()" role="button" class="btn btn-primary" style="margin-right: 1em;">
            <i class="fa fa-arrow-left"></i>
            No wait
        </a>
    </form>
</div>

<@components.footer />