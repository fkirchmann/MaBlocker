<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Create Blocklist">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
</@components.header>

<@navbar.navbar active="blocklists" />

<div class="container">
    <#if message??>
        <div class="alert alert-${message.getCssClass()}" role="alert" style="margin-top: 1em; margin-bottom: 0;">
            ${message.getText()}
        </div>
    </#if>

    <h1>Create Blocklist</h1>

    <form method="post" action="/blocklists/create">
        <label for="exampleInputEmail1">Blocklist name:</label>
        <input type="text" class="form-control" name="blocklist-name" placeholder="Distracting stuff">
        <h2>
            <button type="submit" class="btn btn-success">
                <i class="fa fa-check"></i>
                Do it
            </button>
            <a href="/blocklists" role="button" class="btn btn-danger">
                <i class="fa fa-times"></i>
                Nah I changed my mind
            </a>
        </h2>


        <!--<a class="btn btn-danger" href="/blocklists" role="button">
            <i class="fa fa-times"></i>
            Cancel
        </a>-->
    </form>
</div>

<@components.footer />