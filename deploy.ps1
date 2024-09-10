# Mimic CI/CD pipeline to deploy Java application to remote host
#
# To run this script, you should have PowerShell 7.0 or later installed.
# You can download it at https://github.com/PowerShell/PowerShell/releases
# 
# To auto-deploy Java application to remote host, follow these steps:
#
# 1. Create a .env file with hostConnection and deployPath
#       Example:
#       ```
#       username@hostname
#       /path/to/deploy
#       ```
#       It can also be configured in your .ssh profile. hostConnection is
#       what follows ssh or scp command.
#
# 2. Build Java application.
#       In IntelliJ IDEA, go to maven tab and run 'install' in 'Lifecycle'.
#       This will create a .jar file in target/ directory.
#       Make sure to keep only the latest .jar file in target/ directory.
#
# 3. Run this script under project root directory.
#

# Pack target/*.jar and Dockerfile into tar.gz
Write-Host "Packing target/*.jar and Dockerfile into tar.gz"
$randomString = -join ((65..90) + (97..122) | Get-Random -Count 10 | % { [char]$_ })
$archiveName = "$randomString.tar.gz"
tar -czf $archiveName target/*.jar Dockerfile

# Read first and second line from .env file and raise error if not found
$envFile = Get-Content .env
$hostConnection = $envFile[0]
$deployPath = $envFile[1]
if (-not $hostConnection -or -not $deployPath)
{
    Write-Host "Error: .env file is missing hostConnection or deployPath"
    exit 1
}

# Copy archive to remote host
Write-Host "Copying archive to remote host"
scp $archiveName ${hostConnection}:/tmp/$archiveName

# Execute remote commands:
Write-Host "Executing deploy commands"
$commands = @(
    "buaalogin; ",
    "tar -xzf /tmp/$archiveName -C $deployPath --overwrite",
    " && rm /tmp/$archiveName",
    " && cd $deployPath",
    " && ./deploy.sh"
)
ssh $hostConnection $commands

# Delete archive
Write-Host "Finishing up"
Remove-Item $archiveName
