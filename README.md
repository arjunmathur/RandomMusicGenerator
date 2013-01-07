RandomMusicGenerator
====================

A personal project that can compose music by itself!


The RandomMusicGenerator uses a Markov chain process in order to 'randomly'
create music in the same style as defined corpus of music! Music is written and read
in abc notation (abcnotation.com), a text based language that can be used
to express western music. Because of its format, abc notation can easily
be converted into a MIDI audio file or even sheet music!

There are many people around the world who have transcribed music into this
format. Two good libraries of abc notation songs can be found at 
[lotroinfo.com/abc_library] and [sites.google.com/site/lotroabc/songs].

As input, the program requires an abc 'corpus'. Each abc song contains 
two parts: a header and the music. An abc corpus is defined as a collection
of many different abc songs' music combined into one text file (w/o header).
In order to create one, check the above websites for some songs that 
have something in common (same artist etc.), open them in your favorite 
text editor and copy all the music part into one text file. Drop that
file into the folder of this program, and run it!

The program will export a composed abc file which you can convert to audio at
[http://www.concertina.net/tunes_convert.html] 
or [http://www.mandolintab.net/abcconverter.php]

So go ahead and make your own Beethoven Sonata or addition to the
Zelda soundtrack...


KNOWN LIMITATIONS:

Although simple and effective, there are a few limitations with the use of abc notation:

1. Limited availability: Music must be manually transcribed into abc notation (or composed in MIDI and then converted). Thus, although many great people around the world have done many songs, the majority of songs have not been transcribed. Because the RandomMusicGenerator relies on a thorough corpus to generate good-sounding music of the same style, this is an issue.

this issue is then compounded by...

2. Variation in music style: In order to generate good sounding music, it is best to use a corpus of music with similar style: Same artist, same time signature, same key, etc. However because of the limited availability, this is difficult. 

DESIGN CHOICES TO AMELIORATE ABOVE LIMITATIONS:

1. To create good sounding music, the most important thing is for one key to flow harmoniously to the next. In abc notation, keys are represented by alphabetical characters only, with other characters affecting the tempo, time etc. In order for the markov chain to represent this better, each key in the MarkovMap is a musical word removed of all non-alphabetical characters. This helps to utilize a smaller corpus better and find similiarities in different songs.

2. To counter variations in music style, abc notation can be transposed into different keys at [http://www.mandolintab.net/abcconverter.php]. This was used for the Beatles Corpus.txt.