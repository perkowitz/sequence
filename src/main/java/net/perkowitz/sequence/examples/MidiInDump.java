package net.perkowitz.sequence.examples;

/*
 *	MidiInDump.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 * Copyright (c) 2003 by Florian Bomers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

import java.io.IOException;

import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/*	If the compilation fails because this class is not available,
	get gnu.getopt from the URL given in the comment below.
*/
import gnu.getopt.Getopt;



/**	<titleabbrev>MidiInDump</titleabbrev>
 <title>Listens to a MIDI port and dump the received event to the console</title>

 <formalpara><title>Purpose</title>
 <para>Listens to a MIDI port and dump the received event to the console.</para></formalpara>

 <formalpara><title>Usage</title>
 <para>
 <cmdsynopsis>
 <command>java MidiInDump</command>
 <arg choice="plain"><option>-l</option></arg>
 </cmdsynopsis>
 <cmdsynopsis>
 <command>java MidiInDump</command>
 <arg choice="plain"><option>-d <replaceable>devicename</replaceable></option></arg>
 <arg choice="plain"><option>-n <replaceable>device#</replaceable></option></arg>
 </cmdsynopsis>
 </para></formalpara>

 <formalpara><title>Parameters</title>
 <variablelist>
 <varlistentry>
 <term><option>-l</option></term>
 <listitem><para>list the availabe MIDI devices</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-d <replaceable>devicename</replaceable></option></term>
 <listitem><para>reads from named device (see <option>-l</option>)</para></listitem>
 </varlistentry>
 <varlistentry>
 <term><option>-n <replaceable>device#</replaceable></option></term>
 <listitem><para>reads from device with given index (see <option>-l</option>)</para></listitem>
 </varlistentry>
 </variablelist>
 </formalpara>

 <formalpara><title>Bugs, limitations</title>
 <para>
 For the Sun J2SDK 1.3.x or 1.4.0, MIDI IN does not work. See the <olink targetdoc="faq_midi" targetptr="faq_midi">FAQ</olink> for alternatives.
 </para></formalpara>

 <formalpara><title>Source code</title>
 <para>
 <ulink url="MidiInDump.java.html">MidiInDump.java</ulink>,
 <ulink url="DumpReceiver.java.html">DumpReceiver.java</ulink>,
 <ulink url="MidiCommon.java.html">MidiCommon.java</ulink>,
 <ulink url="http://www.urbanophile.com/arenn/hacking/download.html">gnu.getopt.Getopt</ulink>
 </para>
 </formalpara>

 */
public class MidiInDump
{
    /**	Flag for debugging messages.
     If true, some messages are dumped to the console
     during operation.
     */
    private static boolean		DEBUG = true;


    public static void main(String[] args)
            throws Exception
    {

		/*
		 *	The device name/index to listen to.
		 */
        String	strDeviceName = null;
        int	nDeviceIndex = -1;
        boolean bUseDefaultSynthesizer = false;
        // TODO: synchronize options with MidiPlayer


		/*
		 *	Parsing of command-line options takes place...
		 */
        Getopt	g = new Getopt("MidiInDump", args, "hlsd:n:D");
        int	c;
        while ((c = g.getopt()) != -1)
        {
            switch (c)
            {
                case 'h':
                    printUsageAndExit();

                case 'l':
                    MidiCommon.listDevicesAndExit(true, false);

                case 's':
                    bUseDefaultSynthesizer = true;
                    break;

                case 'd':
                    strDeviceName = g.getOptarg();
                    if (DEBUG) { out("MidiInDump.main(): device name: " + strDeviceName); }
                    break;

                case 'n':
                    nDeviceIndex = Integer.parseInt(g.getOptarg());
                    if (DEBUG) { out("MidiInDump.main(): device index: " + nDeviceIndex); }
                    break;

                case 'D':
                    DEBUG = true;
                    break;

                case '?':
                    printUsageAndExit();

                default:
                    out("MidiInDump.main(): getopt() returned " + c);
                    break;
            }
        }

        if ((strDeviceName == null) && (nDeviceIndex < 0))
        {
            out("device name/index not specified!");
            printUsageAndExit();
        }

        MidiDevice.Info	info;
        if (strDeviceName != null)
        {
            info = MidiCommon.getMidiDeviceInfo(strDeviceName, false);
        }
        else
        {
            info = MidiCommon.getMidiDeviceInfo(nDeviceIndex);
        }
        if (info == null)
        {
            if (strDeviceName != null)
            {
                out("no device info found for name " + strDeviceName);
            }
            else
            {
                out("no device info found for index " + nDeviceIndex);
            }
            System.exit(1);
        }
        MidiDevice	inputDevice = null;
        try
        {
            inputDevice = MidiSystem.getMidiDevice(info);
            inputDevice.open();
        }
        catch (MidiUnavailableException e)
        {
            out(e);
        }
        if (inputDevice == null)
        {
            out("wasn't able to retrieve MidiDevice");
            System.exit(1);
        }
        Receiver	r = new DumpReceiver(System.out);
        try
        {
            Transmitter	t = inputDevice.getTransmitter();
            t.setReceiver(r);
        }
        catch (MidiUnavailableException e)
        {
            out("wasn't able to connect the device's Transmitter to the Receiver:");
            out(e);
            inputDevice.close();
            System.exit(1);
        }
        if (bUseDefaultSynthesizer)
        {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            r = synth.getReceiver();
            try
            {
                Transmitter	t = inputDevice.getTransmitter();
                t.setReceiver(r);
            }
            catch (MidiUnavailableException e)
            {
                out("wasn't able to connect the device's Transmitter to the default Synthesizer:");
                out(e);
                inputDevice.close();
                System.exit(1);
            }
        }
        out("now running; interupt the program with [ENTER] when finished");

        try
        {
            System.in.read();
        }
        catch (IOException ioe)
        {
        }
        inputDevice.close();
        out("Received "+((DumpReceiver) r).seCount+" sysex messages with a total of "+((DumpReceiver) r).seByteCount+" bytes");
        out("Received "+((DumpReceiver) r).smCount+" short messages with a total of "+((DumpReceiver) r).smByteCount+" bytes");
        out("Received a total of "+(((DumpReceiver) r).smByteCount + ((DumpReceiver) r).seByteCount)+" bytes");
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            if (DEBUG) { out(e); }
        }
    }



    private static void printUsageAndExit()
    {
        out("MidiInDump: usage:");
        out("  java MidiInDump -h");
        out("    gives help information");
        out("  java MidiInDump -l");
        out("    lists available MIDI devices");
        out("  java MidiInDump [-D] [-d <input device name>] [-n <device index>]");
        out("    -d <input device name>\treads from named device (see '-l')");
        out("    -n <input device index>\treads from device with given index(see '-l')");
        out("    -D\tenables debugging output");
        System.exit(1);
    }



    private static void out(String strMessage)
    {
        System.out.println(strMessage);
    }



    private static void out(Throwable t)
    {
        if (DEBUG) {
            t.printStackTrace();
        } else {
            out(t.toString());
        }
    }
}



/*** MidiInDump.java ***/
