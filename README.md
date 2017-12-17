# Tweets preprocessor

Author: Igor Tannus Correa

This is a Java algorithm that executes several steps of pre-processing in a database. At first, it was written as a tweet's pre-processor, but it can be adapted to other types of data. 

The pre-processing steps that it can do are:

- remove
  
  - hashtags and citations (#lalaland, @user -> lalaland, user)
  
  - tweets unrelated to the theme according to a list of words (add words to *unrelated.txt*)
  
  - links
  
  - special characters (e.g. ~!@#$%ˆ*&), numbers, and the query term
  
  - stopwords (e.g. a, the, you, with, etc)
  
  - spaces (when there's more than one)
  

- translate

  - slangs and abbreviations (e.g. omg, ily, brb -> oh my god, i love you, be right back -- add words to *dictionary.txt*)
  
  - emoticons (e.g. :], <3 -> happy, love -- add words to *emoticons.txt*)
  
  
- replace

  - uppercase letters to lowercase  
  
  - accented characters (ã, ê, ñ) to unaccented characters (a, e, n)


You can write new steps according to what you need or comment/delete the methods you don't want to use.

This algorithm is part of the paper that I wrote, "Sentiment analysis of tweets related to the movies nominated for the 2017 Academy Awards".

You can [read it](https://igoortc.github.io/research) (in Portuguese) and understand how I used this tool in my paper.

**If you use this tool, please cite the paper :stuck_out_tongue:**
