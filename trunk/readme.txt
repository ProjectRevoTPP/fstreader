fst Reader v0.2.1 by SleepyPrince

This tool is for simplifying the procedure of editing fst.bin.

Editing fst.bin in a Hex editor is error-prone, a typo when changing offset of size of file will crash the game. The fst Reader no only shows you the content of fst.bin files in a GUI, you can easily copy and paste entry info within the tree view.

This program requires Java Runtime Environment (JRE) to run. If you can run java applet in your browser, you can start this program by doubling clicking the "fstReader.jar" file.

limitations:
- table on the right is read only, no change is stored

know issues:
- none yet

TODO:
- direct entry info editing (maybe JTreeTable)
- better search function (maybe wild char/regex)
- handle multiple files

Changes in v0.2.1:
- Bugfix: Cannot open file after closing one
- Bugfix: Window title does not update after saving file

Changes in v0.2.080307:
- New: Improved search function
- New: Retain current tree node selection when reloading