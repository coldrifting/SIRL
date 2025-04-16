package com.coldrifting.sirl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun HtmlView(html: String?) {
    if (html == null) {
        Text("No Steps")
        return
    }

    val steps = html
        .replace("</p>", "</p>###")
        .replace(regex = Regex("(?<image><img.*?>)"), replacement = "\${image}###")
        .split("###")
        .map{s -> s.trim()}
        .filter{s -> s.isNotEmpty()}


    Box(
        Modifier.background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            steps.forEach { s ->
                // Images
                if (s.startsWith("<img")) {
                    val src = Regex("src=['\"](.+?)['\"]").find(s)?.destructured?.toList()?.firstOrNull()
                    if (src != null) {
                        AsyncImage(model = src, contentDescription = null)
                    }
                }
                // Text
                else {
                    Text(AnnotatedString.fromHtml(s))
                }
                Box(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}