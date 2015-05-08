# bibmup

## What it is
Simple program in Java to transform a bibtex library into a mindmap for use with [Mindmup](http://mindmup.com)

## What it is not
Well written

## What it does

It outputs a .mup file which then can be uploaded to [Mindmup](http://mindmup.com) and get something like this 
![Image](https://github.com/torobotaki/bibmup/blob/master/data/out.png?raw=true)

The image above can be optained from the sample in.bib found in the "data" folder.

### Demo
[Click here](https://atlas.mindmup.com/2015/05/fa0da3c0d80701326af24e1209a4d6ff/bibmup_example/index.html) to check out the interactive mindmap example on [MindMup Atlas](http://atlas.mindmup.com). Use "F" on your keyboard to collapse/uncollapse nodes and, "A" to see the attachments.


## What input it expects
### In short
a well-formated BibTeX file. 

### The truth
..is that I have used it only with my .bib exported from my [Mendeley](http://mendeley.com) library.

In particular I exported this by selecting all the documents I wanted and did a right-click, copy, as BibTeX and then pasted that into a .bib file. Why? see below.

## How it works
### Type of nodes

1. For actual bibliographical entries, it creates leaf nodes. It adds the whole BibTeX formatted reference as an attachment.
2. It creates theme nodes, grouping entries, based on the entries keywords and other meta data. These nodes will be coloured randomly, and listed alphabetically top to bottom. 

### Where it gets the categories

Based on my Mendeley experience, I have used keywords (author keywords), mendeley-groups (Mendeley folders) and mendeley-tags (user specified keywords). I normalize them by capitalizing the first letters and removing any funny stuff. 

Mendeley-groups are hierarchical, as they are folders in the library. This is the only thing that can provide hierarchy. BTW, the only way to get this type of metadata I found on Mendeley, was to copy my library as bibtex, not to export it. 

A config file can be provided. Among other things it can allow the user to change the source of these categories to user-provided keys, flat or hierarchical. 

## How to use it

Well, nothing really user friendly can be found here, ....for now?

In short it should take two arguments, input and output file. A possible third argument is the config. See data/ for examples.

Then once you get the output mup file, you need to upload it to MindMup. Notice all nodes (except for the root) should be collapsed by default, because it can get scary. 

## Why

It sounded like a good idea, and it took me one day to make it work. 

### Why in Java

It's not you, it's me. 

It's the only way it could take me a day to code this, currently.
