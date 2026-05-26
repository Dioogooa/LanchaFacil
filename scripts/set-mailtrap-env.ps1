# Defina MAIL_USERNAME e MAIL_PASSWORD com os valores da inbox Mailtrap (SMTP Settings)
param(
    [Parameter(Mandatory = $true)]
    [string]$Username,
    [Parameter(Mandatory = $true)]
    [string]$Password
)

$env:MAIL_HOST = "sandbox.smtp.mailtrap.io"
$env:MAIL_PORT = "2525"
$env:MAIL_USERNAME = $Username
$env:MAIL_PASSWORD = $Password
$env:APP_MAIL_PROVIDER = "mailtrap"
Write-Host "Variaveis MAIL_* definidas para esta sessao do PowerShell."
