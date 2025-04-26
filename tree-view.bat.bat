@echo off
setlocal

REM Получаем имя текущей папки
for %%I in ("%cd%") do set "FOLDER_NAME=%%~nxI"

set "OUTPUT=%FOLDER_NAME%.txt"

if exist "%OUTPUT%" del "%OUTPUT%"

tree /F /A > "%OUTPUT%"

start notepad "%OUTPUT%"
