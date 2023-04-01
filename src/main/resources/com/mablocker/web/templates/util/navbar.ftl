<#macro navbar active="">
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#mainNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>
        <a class="navbar-brand" href="/">MaBlocker</a>

        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item <#if active == "blocklists">active</#if>">
                    <a class="nav-link" href="/blocklists">
                        <i class="fa fa-list"></i> Blocklists
                    </a>
                </li>
                <li class="nav-item <#if active == "status">active</#if>">
                    <a class="nav-link" href="/apply/status">
                        <i class="fa fa-info-circle"></i> Status
                    </a>
                </li>
                <#nested />
            </ul>
        </div>
    </nav>
</#macro>