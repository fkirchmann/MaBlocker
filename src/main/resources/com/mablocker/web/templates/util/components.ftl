<#macro header title>
    <!DOCTYPE html>
    <html>
    <head>
        <title>${title} - MaBlocker</title>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="stylesheet" href="/static/dist/css/bootstrap.min.css" />
        <link rel="stylesheet" href="/static/dist/css/font-awesome.min.css" />
        <link rel="shortcut icon" href="/static/img/favicon.ico" />
        <#nested />
    </head>
    <body>
</#macro>

<#macro footer>
    <script src="/static/dist/js/bootstrap.min.js"></script>
    <#nested />
    </body></html>
</#macro>

<#macro applyChanges>
    <div class="alert alert-warning" role="alert"
         style="margin-top: 1em; margin-bottom: 0; padding-top: 0.5em; padding-bottom: 0.5em;">
        <form class="form-inline" method="post" action="/apply">
            Your changes have not been applied yet.
            <button type="submit" class="btn btn-outline-success" style="margin-left: 0.5em;">
                <i class="fa fa-check"></i>
                Apply now
        </form>
    </div>
</#macro>
