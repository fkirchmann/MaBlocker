<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Apply Blocklists">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
    <#if state == "RUNNING">
        <meta http-equiv="refresh" content="2">
    </#if>
</@components.header>

<@navbar.navbar active="status" />

<div class="container">
    <#if unappliedChanges><@components.applyChanges /></#if>

    <h1>
        <#switch state>
            <#case "IDLE">
                <i class="fa fa-clock-o"></i>
                Standing by for changes
            <#break>
            <#case "RUNNING">
                <div class="spinner-border" role="status" style="vertical-align: baseline;"></div>
                Applying changes...
            <#break>
            <#case "ERROR">
                <i class="fa fa-exclamation-triangle"></i>
                Failed to apply blocklists
            <#break>
            <#case "COMPLETE">
                <i class="fa fa-check"></i>
                Blocklists applied successfully
            <#break>
            <#default>
                Status: ${state}
        </#switch>
    </h1>

    <#if log?has_content>
        <div class="card text-white bg-dark" style="margin-top: 1em;">
            <div class="card-header">
                <i class="fa fa-terminal" style="margin-right: 0.5em"></i>
                Log messages
            </div>
            <div class="card-body" style="padding-top: 0; padding-bottom: 0;">
                <p class="card-text">
                <pre><code class="text-white">${log}</code></pre>
                </p>
            </div>
        </div>
    </#if>
</div>

<@components.footer />