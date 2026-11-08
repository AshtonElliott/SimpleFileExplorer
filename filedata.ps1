Get-Date

echo ~

$folder = $pwd

Get-ChildItem $folder | % { $_.Name,$_.Extension,$_.LastWriteTime,$_.Length -join ';' } | Out-File $folder\spikes\s-01\test.txt

$output = Get-Content $folder\spikes\s-01\test.txt

echo $output

echo ~

Get-Date