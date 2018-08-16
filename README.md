# jedit-plugin-luaj

This plugin comes packaged with LuaJ. LuaJ is a Lua interpreter based on the 5.2.x version of Lua.
See <http://www.luaj.org/luaj/3.0/README.html>.

If the Console plugin is installed, this plugin automatically sets the
system environment variable LUAJ to the path of luaj.
So to run a script called 'script.lua', cd to the file's directory (cd $d) and run:

java -cp "$LUAJ" lua script.lua

LuaJPlugin.jar is necessary for the plugins LuaJShell and LuaJShellSE (LuaJ shell for scripting engine).