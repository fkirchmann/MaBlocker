<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Rename Blocklist">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
</@components.header>

<@navbar.navbar active="blocklists" />

<div class="container">
    <#if message??>
        <div class="alert alert-${message.getCssClass()}" role="alert" style="margin-top: 1em; margin-bottom: 0;">
            ${message.getText()}
        </div>
    </#if>

    <h1>Rename Blocklist ${blocklist.name}</h1>

    <form method="post" action="/blocklist/${blocklist.id?c}/rename">
        <label for="newName">New Blocklist name:</label>
        <input type="text" id="newName" class="form-control" name="new-name" placeholder="hmm">
        <h2>
            <button type="submit" class="btn btn-success">
                <i class="fa fa-check"></i>
                Aye
            </button>
            <a href="/blocklists" role="button" class="btn btn-danger">
                <i class="fa fa-times"></i>
                Not today
            </a>
        </h2>
    </form>
</div>

<@components.footer />