<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Edit Blocklist">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
</@components.header>

<@navbar.navbar active="blocklists" />

<div class="container">
    <#if deleted??>
        <div class="alert alert-success" role="alert" style="margin-top: 1em; margin-bottom: 0;">
            Removed <b>${idnDecoder.apply(deleted)}</b> from the blocklist.
        </div>
    </#if>

    <h1>Edit Blocklist ${blocklist.name}</h1>

    <ul>
        <#list blocklist.listHosts() as host>
            <li class="list-group-item">
                <form id="form-${host}" class="form-inline" method="post"
                    action="/blocklist/${blocklist.id?c}/edit">
                    <input type="hidden" name="delete" value="${host}"></input>
                    <a href="#" onclick="document.getElementById('form-${host}').submit();"
                        style="margin-right: 0.5em;">
                        <i class="fa fa-trash"></i>
                    </a>
                    ${idnDecoder.apply(host)}
                </form>
            </li>
        </#list>
    </ul>
</div>

<@components.footer />