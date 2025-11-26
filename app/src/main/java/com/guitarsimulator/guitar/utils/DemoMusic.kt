package com.guitarsimulator.guitar.utils

import com.guitarsimulator.guitar.dtmodels.NoteEventVM
import com.guitarsimulator.guitar.dtmodels.RecordingVM

object DemoMusic {
    val demoRecordings: List<RecordingVM> = listOf(
        // Bài Mẫu 1: Ode To Joy - Melody
        RecordingVM(
            id = 1001L,
            name = "Ode To Joy",
            durationMs = 15200L,
            sequence = listOf(
                NoteEventVM("E4", 0, 0L),
                NoteEventVM("E4", 0, 500L),
                NoteEventVM("E4", 1, 1000L),
                NoteEventVM("E4", 3, 1500L),

                NoteEventVM("E4", 3, 2000L),
                NoteEventVM("E4", 1, 2500L),
                NoteEventVM("E4", 0, 3000L),
                NoteEventVM("B3", 3, 3500L),

                NoteEventVM("B3", 1, 4000L),
                NoteEventVM("B3", 1, 4500L),
                NoteEventVM("B3", 3, 5000L),
                NoteEventVM("E4", 0, 5500L),

                NoteEventVM("E4", 0, 6000L),
                NoteEventVM("B3", 3, 6700L),
                NoteEventVM("B3", 3, 7000L),

                NoteEventVM("E4", 0, 8000L),
                NoteEventVM("E4", 0, 8500L),
                NoteEventVM("E4", 1, 9000L),
                NoteEventVM("E4", 3, 9500L),

                NoteEventVM("E4", 3, 10000L),
                NoteEventVM("E4", 1, 10500L),
                NoteEventVM("E4", 0, 11000L),
                NoteEventVM("B3", 3, 11500L),

                NoteEventVM("B3", 1, 12000L),
                NoteEventVM("B3", 1, 12500L),
                NoteEventVM("B3", 3, 13000L),
                NoteEventVM("E4", 0, 13500L),

                NoteEventVM("B3", 3, 14000L),
                NoteEventVM("B3", 1, 14700L),
                NoteEventVM("B3", 1, 15000L)

            )
        ),

        // Bài Mẫu 2: Twinkle Twinkle Little Star (Dây G3)
        RecordingVM(
            id = 1002L,
            name = "Twinkle Star",
            durationMs = 9000L,
            sequence = listOf(
                NoteEventVM("G3", 0, 0L),
                NoteEventVM("G3", 0, 300L),
                NoteEventVM("G3", 7, 600L),
                NoteEventVM("G3", 7, 900L),
                NoteEventVM("G3", 9, 1200L),
                NoteEventVM("G3", 9, 1500L),
                NoteEventVM("G3", 7, 1800L),

                NoteEventVM("G3", 5, 2300L),
                NoteEventVM("G3", 5, 2600L),
                NoteEventVM("G3", 4, 2900L),
                NoteEventVM("G3", 4, 3200L),
                NoteEventVM("G3", 2, 3500L),
                NoteEventVM("G3", 2, 3800L),
                NoteEventVM("G3", 0, 4100L),

                NoteEventVM("G3", 7, 4600L),
                NoteEventVM("G3", 7, 4900L),
                NoteEventVM("G3", 5, 5200L),
                NoteEventVM("G3", 5, 5500L),
                NoteEventVM("G3", 4, 5800L),
                NoteEventVM("G3", 4, 6100L),
                NoteEventVM("G3", 2, 6400L),

                NoteEventVM("G3", 7, 6900L),
                NoteEventVM("G3", 7, 7200L),
                NoteEventVM("G3", 5, 7500L),
                NoteEventVM("G3", 5, 7800L),
                NoteEventVM("G3", 4, 8100L),
                NoteEventVM("G3", 4, 8400L),
                NoteEventVM("G3", 2, 8700L)
            )
        ),

    )
}