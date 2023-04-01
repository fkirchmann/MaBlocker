<#import "util/components.ftl" as components>
<#import "util/navbar.ftl" as navbar>

<@components.header title="Blocklists">
    <link href="/static/css/blocklists.css" rel="stylesheet" />
</@components.header>

<@navbar.navbar active="blocklists" />

<div class="container">
    <#if message??>
        <div class="alert alert-${message.getCssClass()}" role="alert" style="margin-top: 1em; margin-bottom: 0;">
            ${message.getText()}
        </div>
    </#if>

    <#if unappliedChanges><@components.applyChanges /></#if>

    <h1>
        Blocklists
        <a href="/blocklists/create">
            <i class="fa fa-plus-circle"></i>
        </a>
    </h1>

    <#list blocklists as blocklist>
    <form id="delete-${blocklist.id?c}" class="form-inline" method="post" action="/blocklist/${blocklist.id?c}/delete">
    </form>

    <div id="accordion-blocklist-${blocklist.id?c}" style="margin-top: 0.2em; margin-bottom: 0.8em;">
        <div class="card">
            <div class="card-header" id="headingOne">
                <form class="form-inline" method="post" action="/blocklists/add/preview">
                    <label style="font-size: 2em; padding-right: 1em;">
                        <#if blocklist.listHosts()?has_content>
                          <a href="#" data-toggle="collapse" data-target="#collapse-blocklist-${blocklist.id?c}">
                              ${blocklist.name}
                          </a>
                        <#else>
                            ${blocklist.name}
                        </#if>
                        <a href="/blocklist/${blocklist.id?c}/rename" style="margin-left: 0.4em;">
                            <i class="fa fa-pencil"></i>
                        </a>

                        <#if !blocklist.listHosts()?has_content>
                            <a href="#" onclick="document.getElementById('delete-${blocklist.id?c}').submit();"
                               style="margin-left: 0.4em;">
                                <i class="fa fa-trash"></i>
                            </a>
                        </#if>
                    </label>
                    <textarea name="new-hosts" class="form-control mb-2 mr-sm-2 autoExpand" data-min-rows='1' rows="1"
                            style="margin-bottom: 0px !important; min-width: 20em;"
                            placeholder="Add website(s), press Enter for multiple"></textarea>
                    <input type="hidden" name="blocklist-id" value="${blocklist.id?c}">
                    <button type="submit" class="btn btn-success mb-2" style="margin-bottom: 0px !important;">
                        <i class="fa fa-plus"></i>
                    </button>
                </form>
            </div>

            <#if blocklist.listHosts()?has_content>
                <div id="collapse-blocklist-${blocklist.id?c}" class="collapse"
                     data-parent="#accordion-blocklist-${blocklist.id?c}">
                  <ul class="list-group list-group-flush">
                    <#list blocklist.listHosts() as host>
                      <li class="list-group-item">${idnDecoder.apply(host)}</li>
                    </#list>
                  </ul>
                </div>
            </#if>
        </div>
    </div>
    </#list>
</div>




<@components.footer>
    <script src="/static/dist/js/autosize.min.js"></script>
    <script type="text/javascript">
        autosize(document.querySelectorAll('textarea'));
    </script>
</@components.footer>